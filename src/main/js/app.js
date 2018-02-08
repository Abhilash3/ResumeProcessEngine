const React = require('react');
const ReactDOM = require('react-dom');
const client = require('./client');

class App extends React.Component {

    constructor(props) {
        super(props);
        this.state = {next: 0, resumes: []};
        this.onScroll = this.onScroll.bind(this);
        this.onClick = this.onClick.bind(this);
    }

    onClick(e) {
        e.preventDefault();
        if (this.state.next === 0) {
            window.addEventListener('scroll', this.onScroll, false);
        }

        var skills = document.querySelector('#skills').value.split(',').map(a => a.trim());
        var experience = document.querySelector('#experience').value;

        this.retrieveResumes({skills, experience}, 0, []);
    }

    componentWillUnmount() {
        window.removeEventListener('scroll', this.onScroll, false);
    }

    retrieveResumes(data, page, resumes) {
        client({
            method: 'POST',
            entity: data,
            path: '/resumes?page=' + page,
            headers: {'Content-Type': 'application/json'}
        }).done(response => {
            this.setState({
                next: page + 1, data,
                done: response.entity.length === 0,
                resumes: resumes.concat(response.entity)
            });
        });
    }

    onScroll() {
        var scrollTop = (document.documentElement && document.documentElement.scrollTop) || document.body.scrollTop;
        var scrollHeight = (document.documentElement && document.documentElement.scrollHeight) || document.body.scrollHeight;
        var clientHeight = document.documentElement.clientHeight || window.innerHeight;

        if (Math.ceil(scrollTop + clientHeight) >= scrollHeight - 100 && !this.state.done) {
            this.retrieveResumes(this.state.data, this.state.next, this.state.resumes);
        }
    }

    render() {
        return (
            <div>
                <div className='input-group'>
                    <input type='text' className='form-control' id='skills'></input>
                    <input type='number' className='form-control' id='experience'></input>
                    <div className='input-group-append'>
                        <span className='input-group-text' onClick={this.onClick}>Search...</span>
                    </div>
                </div>
                <ResumeList resumes={this.state.resumes}/>
            </div>
        );
    }
}

class ResumeList extends React.Component {
    render() {
        var resumes = this.props.resumes.map(resume =>
            <Resume key={resume.fileName} resume={resume}/>
        );
        return (
            <div className="card">
                <ul className="list-group list-group-flush">
                    {resumes}
                </ul>
            </div>
        );
    }
}

class Resume extends React.Component {
    constructor(props) {
        super(props);
        this.onClick = this.onClick.bind(this);
    }

    onClick(e) {
        e.preventDefault();
        window.open(`open/${this.props.resume.id}`);
    }

    render() {
        return (
            <li className="list-group-item list-group-item-action">
                <a className="card-link" onClick={this.onClick}>
                    {this.props.resume.fileName}
                </a>
            </li>
        )
    }
}

ReactDOM.render(
    <App />,
    document.getElementById('react')
)
