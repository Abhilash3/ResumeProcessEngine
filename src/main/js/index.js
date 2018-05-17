const React = require('react');
const ReactDOM = require('react-dom');
const ResumeSearch = require('./resume/resumeSearch');
const StatusList = require('./status/statusList');
const App = require('./app');

class Index extends App {
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
    <Index />,
    document.querySelector('#container')
)