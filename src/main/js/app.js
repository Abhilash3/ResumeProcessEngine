const React = require('react');
const ReactDOM = require('react-dom');
const ResumeSearch = require('./resume/resumeSearch');
const StatusList = require('./status/statusList');
const client = require('./api/client');

class App extends React.Component {
    componentDidMount() {
        window.onerror = function() {
            client({
                method: 'POST',
                entity: arguments[4].stack,
                path: '/service/log/error',
                headers: {'Content-Type': 'text/plain'}
            });
        }
    }

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
    document.querySelector('#app')
)
