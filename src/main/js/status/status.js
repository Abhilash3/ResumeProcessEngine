const React = require('react');

class Status extends React.Component {
    componentDidMount() {
		setTimeout(this.props.close, 1500);
    }

    render() {
	    return (
			<div>
				<div className={`alert alert-${this.props.type} alert-dismissable`}>
					<a href='#' className='close' onClick={this.props.close}>&times;</a>
					{this.props.text}
				</div>
			</div>
		);
	}
}

module.exports = Status;
