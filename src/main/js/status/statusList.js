const React = require('react');
const Status = require('./status');

class StatusList extends React.Component {
    constructor(props) {
        super(props);
        this.state = {list: []};
        this.addStatus = this.addStatus.bind(this);
    }

    componentDidMount() {
        document.addEventListener('display-status', this.addStatus, false);
    }

    componentWillUnmount() {
        document.removeEventListener('display-status', this.addStatus, false);
    }

    addStatus(e) {
        e.preventDefault();
        this.setState({list: [...this.state.list, e.detail]});
    }

    removeStatus(note) {
        this.setState({list: this.state.list.filter(a => a !== note)});
    }

    render() {
        var list = this.state.list.map((item, index) =>
            <Status key={index} item={item} close={() => this.removeStatus(item)} />
        );
        return (<div id='statusList'>{list}</div>);
    }
}

module.exports = StatusList;
