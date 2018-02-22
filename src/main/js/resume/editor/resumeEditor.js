const React = require('react');
const Editor = require('./editor');
const client = require('./../../client');

class ResumeEditor extends Editor {
    constructor(props) {
        super(props, '#resumeEditor', 'resume-editor');
    }

    update({fileName = '', graduation = '', email = ''} = {}) {
        this.dom.querySelector('#title').innerHTML = fileName;
        this.dom.querySelector('#fileName').value = fileName;
        this.dom.querySelector('#graduation').value = graduation;
        this.dom.querySelector('#email').value = email;
    }

    save(e) {
        e.preventDefault();
        var fileName = this.dom.querySelector('#fileName').value;
        var graduation = this.dom.querySelector('#graduation').value;
        var email = this.dom.querySelector('#email').value;

        client({
            method: 'POST',
            entity: {fileName, graduation, email},
            path: `/resume/update?id=${this.resume.id}`,
            headers: {'Content-Type': 'application/json'}
        }).then(() => {
            this.resume.fileName = fileName;
            this.resume.email = email;
            this.resume.graduation = Number(graduation);
            this.resume.experience = new Date().getFullYear() - Number(graduation);
            this.resume = null;

            document.dispatchEvent(new CustomEvent('resume-update'));
            document.dispatchEvent(new CustomEvent('display-status', {detail: {type: 'info', text: 'Changes saved'}}));
            this.close();
        }).catch(response => {
            console.log(response);
            document.dispatchEvent(new CustomEvent('display-status', {detail: {type: 'danger', text: 'Update failed'}}));
        });
    }

    render() {
        return (
            <div id='resumeEditor' className='modal backdrop'>
                <div className='modal-dialog modal-dialog-centered'>
                    <div className='modal-content'>
                        <div className='modal-header'>
                            <h5 className='modal-title' id='title'></h5>
                            <button type='button' className='close' onClick={this.cancel}>
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                        <div className='modal-body'>
                            <div className='form-group'>
                                <label>File Name: </label>
                                <input type='text' className='form-control' id='fileName'></input>
                            </div>
                            <div className='form-group'>
                                <label>Graduation: </label>
                                <input type='number' className='form-control' id='graduation'></input>
                            </div>
                            <div className='form-group'>
                                <label>Email: </label>
                                <input type='text' className='form-control' id='email'></input>
                            </div>
                        </div>
                        <div className='modal-footer'>
                            <button type='button' className='btn btn-secondary' onClick={this.cancel}>Close</button>
                            <button type='button' className='btn btn-primary' onClick={this.save}>Save</button>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}

module.exports = ResumeEditor;
