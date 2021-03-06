const React = require('react');
const ReactDOM = require('react-dom');
const GroupingList = require('./grouping/groupingList');
const Confirm = require('./modal/confirm');
const StatusList = require('./status/statusList');
const App = require('./app');

class Conf extends App {
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
    document.querySelector('#container')
)
