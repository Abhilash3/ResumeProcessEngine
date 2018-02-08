const React = require('react');
const ReactDOM = require('react-dom');
const client = require('./client');

class App extends React.Component {
    render() {
        return (
            <ResumeSearch />
        );
    }
}

class ResumeSearch extends React.Component {

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

        var skills = document.querySelector('#skills').value.split(',')
            .map(a => a.trim()).filter(a => a.length > 0);

        var experience = document.querySelector('#exp').value;

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
                    <div className='input-group-prepend'>
                        <span className='input-group-text'>Total </span>
                    </div>
                    <input type='number' className='form-control' id='exp'></input>
                    <div className='input-group-append'>
                        <span className='input-group-text'>years of experience in </span>
                    </div>
                    <input type='text' className='form-control' id='skills'></input>
                    <div className='input-group-append'>
                        <button className='btn btn-outline-secondary' type='button' onClick={this.onClick}>Search...</button>
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
            <table className='table table-hover'>
                {resumes}
            </table>
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
            <tr>
                <td>{this.props.resume.id}</td>
                <td>
                    <a href='#' className="card-link" onClick={this.onClick}>{this.props.resume.fileName}</a>
                </td>
                <td>{this.props.resume.exp}</td>
                <td>{this.props.resume.skills.join(', ')}</td>
            </tr>
        )
    }
}

ReactDOM.render(
    <App />,
    document.getElementById('react')
)
