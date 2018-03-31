const React = require('react');
const Popup = require('./popup');

class Editor extends Popup {
    constructor(props, selector, event) {
        super(props, selector, event);

        this.save = this.save.bind(this);
        this.cancel = this.cancel.bind(this);
        this.keyup = this.keyup.bind(this);
    }

    componentDidMount() {
        super.componentDidMount();
        this.dom.addEventListener('keyup', this.keyup, false);
    }

    componentWillUnmount() {
        super.componentWillUnmount();
        this.dom.removeEventListener('keyup', this.keyup, false);
    }

    cancel(e) {
        e.preventDefault();
        this.close();
    }

    save() {
        throw new Error('Not Supported');
    }

    keyup(e) {
        e.preventDefault();
        if (e.keyCode === 27) {
            this.close();
        }
    }
}

module.exports = Editor;
