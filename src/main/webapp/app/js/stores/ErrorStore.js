/**
 * Author: Bill Lv<ideaalloc@gmail.com>
 * Date: 2015-02-09
 */
var AppDispatcher = require('../dispatcher/AppDispatcher');
var EventEmitter = require('events').EventEmitter;
var ErrorConstants = require('../constants/ErrorConstants');
var assign = require('object-assign');

var CHANGE_EVENT = 'change';

var _messages = {};

function create(text) {
    _messages = {
        text: text
    };
}

function destroy() {
    delete _messages;
}

var ErrorStore = assign({}, EventEmitter.prototype, {

    /**
     * Get the entire collection of TODOs.
     * @return {object}
     */
    getAll: function() {
        return _messages;
    },

    emitChange: function() {
        this.emit(CHANGE_EVENT);
    },

    /**
     * @param {function} callback
     */
    addChangeListener: function(callback) {
        this.on(CHANGE_EVENT, callback);
    },

    /**
     * @param {function} callback
     */
    removeChangeListener: function(callback) {
        this.removeListener(CHANGE_EVENT, callback);
    }
});

// Register callback to handle all updates
AppDispatcher.register(function(action) {
    var text;

    switch(action.actionType) {
        case ErrorConstants.ERROR_CREATE:
            text = action.text.trim();
            if (text !== '') {
                create(text);
            }
            ErrorStore.emitChange();
            break;

        case ErrorConstants.ERROR_DESTROY:
            destroy();
            ErrorStore.emitChange();
            break;

        default:
        // no op
    }
});

module.exports = ErrorStore;