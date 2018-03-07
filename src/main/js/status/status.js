const React = require('react');

class Status extends React.Component {
    constructor(props) {
        super(props);
        this.item = this.props.item;
    }

    componentDidMount() {
        if (!this.item.imp) {
		    setTimeout(this.props.close, this.item.timeout || 5000);
		}
    }

    render() {
	    return (
			<div className='status'>
				<div className={`alert alert-${this.item.type || 'secondary'}`}>
					<a href='#' className='close' onClick={this.props.close}>&times;</a>
					{this.item.text}
				</div>
			</div>
		);
	}
}

module.exports = Status;
