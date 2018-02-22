const React = require('react');

class Editor extends React.Component {
    constructor(props, elementId, event) {
        super(props);
        this.elementId = elementId;
        this.event = event;

        this.save = this.save.bind(this);
        this.cancel = this.cancel.bind(this);
        this.open = this.open.bind(this);
    }

    componentDidMount() {
        this.dom = document.querySelector(this.elementId);
        document.addEventListener(this.event, this.open, false);
    }

    componentWillUnmount() {
        document.removeEventListener(this.event, this.open, false);
    }

    open(e) {
        this.resume = e.detail;
        this.dom.classList.toggle('show');
        this.update(this.resume);
    }

    close() {
        this.dom.classList.toggle('show');
        this.update();
    }

    cancel(e) {
        e.preventDefault();
        this.close();
    }

    save() {
        throw new Error('Not Supported');
    }

    update() {
        throw new Error('Not Supported');
    }

    render() {
        throw new Error('Not Supported');
    }
}

module.exports = Editor;
