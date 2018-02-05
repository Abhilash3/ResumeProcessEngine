const React = require('react');
const ReactDOM = require('react-dom');
const client = require('./client');

const buttonClass = 'btn btn-default';

class App extends React.Component {

    constructor(props) {
        super(props);
        this.state = {resumes: []};
    }

    componentDidMount() {
        client({method: 'GET', path: '/api/resumes'}).done(response => {
            this.setState({resumes: response.entity._embedded.resumes});
        });
    }

    render() {
        return (
            <ResumeList resumes={this.state.resumes}/>
        )
    }
}

class ResumeList extends React.Component{
    render() {
        var resumes = this.props.resumes.map(resume =>
            <Resume key={resume._links.self.href} resume={resume}/>
        );
        return (
            <table className='table table-bordered table-hover'>
                <tbody>
                    <tr>
                        <th>File Name</th>
                        <th>Links</th>
                    </tr>
                    {resumes}
                </tbody>
            </table>
        )
    }
}

class Resume extends React.Component {
    constructor(props) {
        super(props);
        this.handleClick = this.handleClick.bind(this);
    }

    handleClick(e) {
        e.preventDefault();
        window.open(`open/${this.props.resume.content.id}`);
    }

    render() {
        return (
            <tr>
                <td>{this.props.resume.content.fileName}</td>
                <td><button className={buttonClass} onClick={this.handleClick}>Open</button></td>
            </tr>
        )
    }
}

ReactDOM.render(
    <App />,
    document.getElementById('react')
)
