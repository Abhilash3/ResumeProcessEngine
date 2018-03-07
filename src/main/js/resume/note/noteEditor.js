const React = require('react');
const Editor = require('../../modal/editor');
const client = require('./../../api/client');

class NoteEditor extends Editor {
    constructor(props) {
        super(props, '#noteEditor', 'note-editor');
    }

    update({fileName = '', notes = ''} = {}) {
        this.dom.querySelector('#title').innerHTML = fileName;
        this.dom.querySelector('#notes').value = notes;
        this.dom.querySelector('#notes').focus();
    }

    save(e) {
        e.preventDefault();
        var resume = this.details;
        var notes = this.dom.querySelector('#notes').value;

        client({
            method: 'POST',
            entity: {id: resume.id, field: 'notes', content: notes},
            path: '/resume/update',
            headers: {'Content-Type': 'application/json'}
        }).then(() => {
            resume.notes = notes;
            document.dispatchEvent(new CustomEvent('display-status', {detail: {text: 'Notes saved'}}));
        }, response => {
            console.log(response);
            document.dispatchEvent(new CustomEvent('display-status', {detail: {imp: true, type: 'danger', text: 'Save failed'}}));
        });
        this.close();
    }

    render() {
        return (
            <div id='noteEditor' className='modal fade backdrop'>
                <div className='modal-dialog modal-lg modal-dialog-centered'>
                    <div className='modal-content'>
                        <div className='modal-header'>
                            <h5 className='modal-title' id='title'></h5>
                            <button type='button' className='close' onClick={this.cancel}>
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                        <div className='modal-body'>
                            <textarea id='notes' className='form-control'></textarea>
                        </div>
                        <div className='modal-footer'>
                            <button type='button' className='btn btn-light' onClick={this.cancel}>Cancel</button>
                            <button type='button' className='btn btn-dark' onClick={this.save}>Save</button>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}

module.exports = NoteEditor;
