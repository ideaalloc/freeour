/**
 * Author: Bill Lv<ideaalloc@gmail.com>
 * Date: 2015-02-09
 */
var React = require('react');

var Error = require('./Error.react');
var ErrorStore = require('../stores/ErrorStore');

function getErrorState() {
    return {
        allErrors: ErrorStore.getAll()
    };
}

var ErrorApp = React.createClass({

    getInitialState: function () {
        return getErrorState();
    },

    componentDidMount: function () {
        ErrorStore.addChangeListener(this._onChange);
    },

    componentWillUnmount: function () {
        ErrorStore.removeChangeListener(this._onChange);
    },

    /**
     * @return {object}
     */
    render: function () {
        return (
            <Error message={this.state.allErrors} />
        );
    },

    /**
     * Event handler for 'change' events coming from the TodoStore
     */
    _onChange: function () {
        this.setState(getErrorState());
    }

});

module.exports = ErrorApp;
