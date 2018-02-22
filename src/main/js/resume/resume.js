const React = require('react');

class Resume extends React.Component {
    render() {
        return (
            <tr>
                <td>{this.props.index + 1}</td>
                <td><a href='#' data-id={this.props.resume.id} className='card-link open-file'>{this.props.resume.fileName}</a></td>
                <td>{this.props.resume.email}</td>
                <td>{this.props.resume.experience}</td>
                <td><button data-id={this.props.resume.id} className='btn btn-light note-editor' type='button'>Notes...</button></td>
                <td><button data-id={this.props.resume.id} className='btn btn-light resume-editor' type='button'>Edit</button></td>
            </tr>
        )
    }
}

module.exports = Resume;
