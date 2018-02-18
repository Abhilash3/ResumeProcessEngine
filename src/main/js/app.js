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

    componentWillMount() {
        window.addEventListener('scroll', this.onScroll, false);
    }

    componentWillUnmount() {
        window.removeEventListener('scroll', this.onScroll, false);
    }

    onClick(e) {
        e.preventDefault();

        var skills = document.querySelector('#skills').value.split(',')
            .map(a => a.trim()).filter(a => a.length > 0);

        var experience = [
            document.querySelector('#minExp').value || 0,
            document.querySelector('#maxExp').value || 100,
        ];

        var sort = document.querySelector('#sort').value;

        this.retrieveResumes({skills, experience, sort}, 0, []);
    }

    retrieveResumes(data, page, resumes) {
        this.setState({pending: true});
        client({
            method: 'POST',
            entity: data,
            path: `/resume/search?page=${page}`,
            headers: {'Content-Type': 'application/json'}
        }).done(response => {
            this.setState({
                next: page + 1, data, pending: false,
                elementsReceived: response.entity.length !== 0,
                resumes: [...resumes, ...response.entity]
            });
            this.onScroll();
        });
    }

    onScroll() {
        var scrollTop = (document.documentElement && document.documentElement.scrollTop) || document.body.scrollTop;
        var scrollHeight = (document.documentElement && document.documentElement.scrollHeight) || document.body.scrollHeight;
        var clientHeight = document.documentElement.clientHeight || window.innerHeight;

        if (Math.ceil(scrollTop + clientHeight) >= scrollHeight - 100 && this.state.elementsReceived && !this.state.pending) {
            this.retrieveResumes(this.state.data, this.state.next, this.state.resumes);
        }
    }

    onBlur(e) {
        e.preventDefault();

        var minExpInput = document.querySelector('#minExp');
        var maxExpInput = document.querySelector('#maxExp');

        var minValue = Number(minExpInput.value || 0);
        var maxValue = Number(maxExpInput.value || 100);

        if (minValue > maxValue) {
            if (e.target === minExpInput) {
                maxExpInput.value = minValue;
            } else if (e.target === maxExpInput) {
                minExpInput.value = maxValue;
            }
        }
    }

    render() {
        return (
            <div>
                <div className='input-group'>
                    <input type='number' className='form-control' min='0' placeholder='Min...' id='minExp' onBlur={this.onBlur}></input>
                    <input type='number' className='form-control' min='0' placeholder='Max...' id='maxExp' onBlur={this.onBlur}></input>
                    <div className='input-group-append'>
                        <span className='input-group-text'>years of experience in</span>
                    </div>
                    <input type='text' className='form-control' placeholder='Skills...' id='skills'></input>
                    <div className='input-group-append'>
                        <span className='input-group-text'>sorted by</span>
                    </div>
                    <select className='custom-select' id='sort'>
                        <option value='relevance'>Relevance</option>
                        <option value='experience'>Experience</option>
                    </select>
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
        var resumes = this.props.resumes.map((resume, index) =>
            <Resume key={resume.id} index={index} resume={resume}/>
        );
        return (
            <div className='table-responsive'>
                <table className='table table-hover'>
                    <thead>
                        <tr>
                            <th>#</th>
                            <th>Resume</th>
                            <th>Email</th>
                            <th>Experience</th>
                        </tr>
                    </thead>
                    <tbody>
                        {resumes}
                    </tbody>
                </table>
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
        window.open(`/resume/open/${this.props.resume.fileName}`);
    }

    render() {
        return (
            <tr>
                <td>{this.props.index + 1}</td>
                <td><a href='#' className='card-link' onClick={this.onClick}>{this.props.resume.fileName}</a></td>
                <td>{this.props.resume.email}</td>
                <td>{this.props.resume.experience}</td>
            </tr>
        )
    }
}

ReactDOM.render(
    <App />,
    document.querySelector('#container')
)
