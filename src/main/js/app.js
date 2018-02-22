const React = require('react');
const ReactDOM = require('react-dom');
const ResumeSearch = require('./resume/resumeSearch');
const StatusList = require('./status/statusList');

class App extends React.Component {
    render() {
        return (
            <div>
                <ResumeSearch />
                <StatusList />
            </div>
        );
    }
}

ReactDOM.render(
    <App />,
    document.querySelector('#container')
)
