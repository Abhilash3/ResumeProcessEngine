const React = require('react');

class Status extends React.Component {
    constructor(props) {
        super(props);
        this.item = this.props.item;
        this.remove = this.remove.bind(this);
    }

    shouldComponentUpdate() {
        return false;
    }

    componentDidMount() {
        if (!this.item.imp) {
            setTimeout(this.props.close, this.item.timeout || 5000);
        }
    }

    remove(e) {
        e.preventDefault();
        this.props.close();
    }

    render() {
        return (
            <div className='status'>
                <div className={`alert alert-${this.item.type || 'secondary'}`}>
                    <a href='#' className='close' onClick={this.remove}>&times;</a>
                    {this.item.text}
                </div>
            </div>
        );
    }
}

module.exports = Status;
