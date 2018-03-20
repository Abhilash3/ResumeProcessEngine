const React = require('react');

class Popup extends React.Component {
    constructor(props, selector, event) {
        super(props);
        this.selector = selector;
        this.event = event;
        this.open = this.open.bind(this);
        this.close = this.close.bind(this);
        this.keyup = this.keyup.bind(this);
    }

    componentDidMount() {
        this.dom = document.querySelector(this.selector);
        document.addEventListener(this.event, this.open, false);
        this.dom.addEventListener('keyup', this.keyup, false);
    }

    componentWillUnmount() {
        document.removeEventListener(this.event, this.open, false);
        this.dom.removeEventListener('keyup', this.keyup, false);
    }

    open(e) {
        e.preventDefault();
        this.details = e.detail;
        this.dom.classList.toggle('show');
        this.update(this.details);
    }

    close() {
        this.details = null;
        this.dom.classList.toggle('show');
        this.update();
    }

    update() {
        throw new Error('Not Supported');
    }

    render() {
        throw new Error('Not Supported');
    }

    keyup(e) {
        e.preventDefault();
        if (e.keyCode === 27) {
            this.close();
        }
    }
}

module.exports = Popup;
