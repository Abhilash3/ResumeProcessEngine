const React = require('react');
const client = require('./api/client');

class App extends React.Component {
    componentWillMount() {
        window.onerror = function() {
            client({
                method: 'POST',
                entity: arguments[4].stack,
                path: '/service/log/error',
                headers: {'Content-Type': 'text/plain'}
            });
        }
    }
}

module.exports = App;
