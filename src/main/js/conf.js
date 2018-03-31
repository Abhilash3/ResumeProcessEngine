const React = require('react');
const ReactDOM = require('react-dom');
const GroupingList = require('./grouping/groupingList');
const Confirm = require('./modal/confirm');
const StatusList = require('./status/statusList');
const client = require('./api/client');

class Conf extends React.Component {
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
                <GroupingList />
                <Confirm />
                <StatusList />
            </div>
        );
    }
}

ReactDOM.render(
    <Conf />,
    document.querySelector('#conf')
)
