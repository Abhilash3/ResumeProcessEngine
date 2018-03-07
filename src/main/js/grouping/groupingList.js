const React = require('react');
const Grouping = require('./grouping');
const client = require('../api/client');

class GroupingList extends React.Component {
    constructor(props) {
        super(props);
        this.state = {groupings: []};
        this.onKeyup = this.onKeyup.bind(this);
        this.onClick = this.onClick.bind(this);
        this.refresh = this.refresh.bind(this);
        this.newGrouping = this.newGrouping.bind(this);
    }

    componentDidMount() {
        this.dom = document.querySelector('#groupings');
        this.dom.addEventListener('keyup', this.onKeyup);
        this.refresh();
    }

    onKeyup(e) {
        e.preventDefault();
        if (!e.target.classList.contains('grouping-text')) {
            return;
        }

        var dom = e.target.closest('.grouping');

        if (e.keyCode === 27) {
            dom.querySelector('.btn.cancel').click();
        } else if (e.keyCode === 13) {
            dom.querySelector('.btn.save').click();
        }
    }

    refresh() {
        client({
            method: 'GET',
            path: '/grouping/retrieve',
            headers: {'Accept' : 'application/json'}
        }).then(response => {
            this.update(response.entity.map(a => ({keywords: a, edit: false})));
        }, response => {
            console.log(response);
            document.dispatchEvent(new CustomEvent('display-status', {detail: {imp: true, type: 'danger', text: 'Error while refresh'}}));
        });
    }

    newGrouping(e) {
        e.preventDefault();

        var groupings = this.dom.querySelectorAll('input.grouping-text');
        var emptyGrouping = Array.prototype.find.call(groupings, a => !a.value.trim().length);
        if (emptyGrouping) {
            emptyGrouping.focus();
        } else {
            let index = this.state.groupings.length;
            let callback = () => this.dom.querySelector(`#grouping-${index} input.grouping-text`).focus();
            this.update([...this.state.groupings, {keywords: [], edit: true}], callback);
        }
    }

    deleteGrouping(index, callback) {
        client({
            method: 'DELETE',
            entity: {keywords: this.state.groupings[index].keywords},
            path: '/grouping/delete',
            headers: {'Content-Type': 'application/json'}
        }).then(response => {
            this.update(this.state.groupings.filter((a, b) => b !== index), callback);
        }, response => {
            console.log(response);
            document.dispatchEvent(new CustomEvent('display-status', {detail: {imp: true, type: 'danger', text: 'Remove failed'}}));
        });
    }

    saveGrouping(keywords, callback) {
        client({
            method: 'POST',
            entity: {keywords},
            path: '/grouping/save',
            headers: {'Content-Type': 'application/json'}
        }).then(response => {
            this.state.groupings[this.state.groupings.length - 1].keywords = keywords;
            this.update(this.state.groupings, callback);
        }, response => {
            console.log(response);
            document.dispatchEvent(new CustomEvent('display-status', {detail: {imp: true, type: 'danger', text: 'Save failed'}}));
        });
    }

    updateGrouping(index, newKeywords, callback) {
        var oldKeywords = this.state.groupings[index].keywords;
        if (oldKeywords.slice().sort().join() === newKeywords.slice().sort().join()) {
            document.dispatchEvent(new CustomEvent('display-status', {detail: {text: 'No change detected'}}));
            return;
        }

        client({
            method: 'POST',
            entity: {old_version: oldKeywords, new_version: newKeywords},
            path: '/grouping/update',
            headers: {'Content-Type': 'application/json'}
        }).then(response => {
            this.state.groupings[index].keywords = newKeywords;
            this.update(this.state.groupings, callback);
        }, response => {
            console.log(response);
            document.dispatchEvent(new CustomEvent('display-status', {detail: {imp: true, type: 'danger', text: 'Update failed'}}));
        });
    }

    onClick(e) {
        e.preventDefault();
        if (!e.target.classList.contains('btn')) {
            return;
        }

        var index = +e.target.dataset.index;
        var dom = this.dom.querySelector(`#grouping-${index}`);
        var input = dom.querySelector('input.grouping-text');

        var oldKeywords = this.state.groupings[index].keywords;
        var newKeywords = input.value.split(',').map(a => a.trim().toLowerCase()).filter(a => a.length);

        var toggleState = callback => {
            this.state.groupings[index].edit = !this.state.groupings[index].edit;
            this.update(this.state.groupings, callback);
        };

        var reset = () => {
            toggleState();
            input.value = oldKeywords.join(', ');
        };

        var saveNew = () => {
            toggleState();
            this.saveGrouping(newKeywords);
        };

        var update = () => {
            toggleState();
            this.updateGrouping(index, newKeywords);
        };

        var resetPostDeleteWith = inputValues => {
            return () => this.state.groupings.forEach((a, b) => {
                this.dom.querySelector(`#grouping-${b} input.grouping-text`).value = inputValues[b];
            });
        };

        var remove = () => {
            var inputs = this.dom.querySelectorAll(`.grouping:not(:nth-child(${index + 1})) input.grouping-text`);
            var inputValues = Array.prototype.map.call(inputs, a => a.value);

            this.deleteGrouping(index, resetPostDeleteWith(inputValues));
        };

        var confirmRemove = () => {
            if (confirm('Empty grouping will be deleted, are you sure?')) {
                remove();
            }
        };

        var confirmEmpty = () => {
            if (confirm('Empty grouping will be ignored, are you sure?')) {
                this.update(this.state.groupings.filter((a, b) => b !== index));
            }
        };

        var edit = () => toggleState(() => input.focus());

        var saveOperations = [confirmEmpty, confirmRemove, saveNew, update];
        var saveOption = +(!!oldKeywords.length) + 2 * +(!!newKeywords.length);
        var save = saveOperations[saveOption];

        var cancel = oldKeywords.length ? reset : confirmEmpty;

        var operations = {remove, edit, save, cancel};
        operations[e.target.dataset.intent]();
    }

    update(groupings = [], callBack) {
        this.setState({groupings}, callBack);
    }

    render() {
        var groupings = this.state.groupings.map((grouping, index) =>
            <Grouping key={index} index={index} grouping={grouping}/>
        );
        return (
            <div className='jumbotron'>
                <div className='row'>
                    <h4 className='col-2 col-centered'>Groupings:</h4>
                    <div className='col'>
                        <ul id='groupings' className='list-inline' onClick={this.onClick}>
                            {groupings}
                        </ul>
                    </div>
                </div>
                <div className='btn-group float-right'>
                    <button className='btn btn-secondary' onClick={this.newGrouping}>New Grouping</button>
                    <button className='btn btn-secondary' onClick={this.refresh}>Refresh</button>
                </div>
            </div>
        );
    }
}

module.exports = GroupingList;
