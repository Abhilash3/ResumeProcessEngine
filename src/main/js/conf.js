const React = require('react');
const ReactDOM = require('react-dom');
const GroupingList = require('./grouping/groupingList');
const StatusList = require('./status/statusList');

class Configuration extends React.Component {
    render() {
        return (
            <div>
                <GroupingList />
                <StatusList />
            </div>
        );
    }
}

ReactDOM.render(
    <Configuration />,
    document.querySelector('#configuration')
)
