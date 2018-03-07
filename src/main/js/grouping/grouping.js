const React = require('react');

class Grouping extends React.Component {
    render() {
        var index = this.props.index;
        var editMode = this.props.grouping.edit;
        var format = editMode && 'container' || 'list-inline-item';
        var edit = !editMode && 'hide' || '';
        var display = editMode && 'hide' || '';
        var groupingText = this.props.grouping.keywords.join(', ');
        return (
            <li id={`grouping-${index}`} className={`grouping ${format}`}>
                <div className='input-group'>
                    <span className={`grouping-text input-group-text ${display}`}>{groupingText}</span>
                    <input className={`grouping-text form-control ${edit}`} type='text' defaultValue={groupingText} />
                    <div className='input-group-append'>
                        <button data-index={index} data-intent='edit' className={`edit btn btn-dark ${display}`}>Edit</button>
                        <button data-index={index} data-intent='save' className={`save btn btn-dark ${edit}`}>Save</button>
                        <button data-index={index} data-intent='remove' className={`remove btn btn-light ${display}`}>Remove</button>
                        <button data-index={index} data-intent='cancel' className={`cancel btn btn-light ${edit}`}>Cancel</button>
                    </div>
                </div>
            </li>
        );
    }
}

module.exports = Grouping;
