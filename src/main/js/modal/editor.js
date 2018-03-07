const React = require('react');
const Popup = require('./popup');

class Editor extends Popup {
    constructor(props, selector, event) {
        super(props, selector, event);

        this.save = this.save.bind(this);
        this.cancel = this.cancel.bind(this);
    }

    cancel(e) {
        e.preventDefault();
        this.close();
    }

    save() {
        throw new Error('Not Supported');
    }
}

module.exports = Editor;
