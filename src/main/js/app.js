const React = require('react');
const ReactDOM = require('react-dom');
const client = require('./client');

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
			<Resume key={resume._links.self.href} resume={resume.content}/>
		);
		return (
			<table>
				<tbody>
					<tr>
						<th>Id</th>
						<th>Content</th>
					</tr>
					{resumes}
				</tbody>
			</table>
		)
	}
}

class Resume extends React.Component {
	render() {
		return (
			<tr>
				<td>{this.props.resume.id}</td>
				<td>{this.props.resume.content}</td>
			</tr>
		)
	}
}

ReactDOM.render(
	<App />,
	document.getElementById('react')
)
