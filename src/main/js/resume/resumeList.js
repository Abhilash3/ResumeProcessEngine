const React = require('react');
const Resume = require('./resume');
const NoteEditor = require('./editor/noteEditor');
const ResumeEditor = require('./editor/resumeEditor');

class ResumeList extends React.Component {
    constructor(props) {
        super(props);
        this.onClick = this.onClick.bind(this);
    }

    onClick(e) {
        e.preventDefault();

        if (e.target.classList.contains('open-file')) {
            return window.open(`/resume/open?id=${e.target.dataset.id}`);
        }

        if (e.target.classList.contains('note-editor')) {
            let resume = this.props.resumes.find(a => a.id === e.target.dataset.id);
            return document.dispatchEvent(new CustomEvent('note-editor', { detail: resume }));
        }

        if (e.target.classList.contains('resume-editor')) {
            let resume = this.props.resumes.find(a => a.id === e.target.dataset.id);
            return document.dispatchEvent(new CustomEvent('resume-editor', { detail: resume }));
        }
    }

    render() {
        var resumes = this.props.resumes.map((resume, index) =>
            <Resume key={resume.id} index={index} resume={resume}/>
        );
        return (
            <div>
                <div className='table-responsive'>
                    <table className='table table-hover'>
                        <thead>
                            <tr>
                                <th>#</th>
                                <th>Resume</th>
                                <th>Email</th>
                                <th>Experience</th>
                                <th>Notes</th>
                                <th>Edit</th>
                            </tr>
                        </thead>
                        <tbody onClick={this.onClick}>
                            {resumes}
                        </tbody>
                    </table>
                </div>
                <NoteEditor />
                <ResumeEditor />
            </div>
        );
    }
}

module.exports = ResumeList;
