/**
 * Author: Bill Lv<ideaalloc@gmail.com>
 * Date: 2015-02-09
 */
var React = require('react');
var ReactPropTypes = React.PropTypes;

var Error = React.createClass({

    propTypes: {
        message: ReactPropTypes.string.isRequired
    },

    render: function () {
        return (
            <div id="error" class="ui error warning form segment">
                <div class="ui error message">
                    <div class="header">注册失败</div>
                    <p>{this.props.message}</p>
                </div>
            </div>
        );
    }

});

module.exports = Error;