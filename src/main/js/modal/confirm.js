const React = require('react');
const Popup = require('./popup');

class Confirm extends Popup {
    constructor(props, selector, event) {
        super(props, '#confirm', 'confirm');
        this.confirm = this.confirm.bind(this);
    }

    confirm(e) {
        e.preventDefault();
        this.details.callback();
        this.close();
    }

    update({ques = ''} = {}) {
        this.dom.querySelector('#ques').innerHTML = ques;
    }

    render() {
        return (
            <div id='confirm' className='modal fade backdrop'>
                <div className='modal-dialog modal-lg modal-dialog-centered'>
                    <div className='modal-content'>
                        <div className='modal-header'>
                            <h5 className='modal-title' id='title'>Are you sure?</h5>
                            <button type='button' className='close' onClick={this.close}>
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                        <div className='modal-body'>
                            <h2 id='ques'></h2>
                        </div>
                        <div className='modal-footer'>
                            <button type='button' className='btn btn-light' onClick={this.close}>Deny</button>
                            <button type='button' className='btn btn-dark' onClick={this.confirm}>Confirm</button>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}

module.exports = Confirm;
