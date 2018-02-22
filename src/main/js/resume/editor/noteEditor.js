const React = require('react');
const Editor = require('./editor');
const client = require('./../../client');

class NoteEditor extends Editor {
    constructor(props) {
        super(props, '#noteEditor', 'note-editor');
    }

    update({fileName = '', notes = ''} = {}) {
        this.dom.querySelector('#title').innerHTML = fileName;
        this.dom.querySelector('#area').value = notes;
        this.dom.querySelector('#area').focus();
    }

    save(e) {
        e.preventDefault();
        var notes = this.dom.querySelector('#area').value;

        client({
            method: 'POST',
            entity: notes,
            path: `/resume/notes?id=${this.resume.id}`,
            headers: {'Content-Type': 'text/plain'}
        }).then(() => {
            this.resume.notes = notes;
            this.resume = null;

            document.dispatchEvent(new CustomEvent('display-status', {detail: {type: 'info', text: 'Notes saved'}}));
            this.close();
        }).catch(response => {
            console.log(response);
            document.dispatchEvent(new CustomEvent('display-status', {detail: {type: 'danger', text: 'Save failed'}}));
        });
    }

    render() {
        return (
            <div id='noteEditor' className='modal backdrop'>
                <div className='modal-dialog modal-lg modal-dialog-centered'>
                    <div className='modal-content'>
                        <div className='modal-header'>
                            <h5 className='modal-title' id='title'></h5>
                            <button type='button' className='close' onClick={this.cancel}>
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                        <div className='modal-body'>
                            <textarea id='area' className='form-control'></textarea>
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

module.exports = NoteEditor;
