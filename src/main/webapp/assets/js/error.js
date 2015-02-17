/**
 * Author: Bill Lv<ideaalloc@gmail.com>
 * Date: 2015-02-09
 */
var ReactPropTypes = React.PropTypes;

var Error = React.createClass({

    propTypes: {
        message: ReactPropTypes.string.isRequired
    },

    render: function () {
        return (
            <div id="error" className="ui error warning form segment">
                <div className="ui error message">
                    <div className="header">注册失败</div>
                    <p>{this.props.message}</p>
                </div>
            </div>
        );
    }

});

var displayError = function (message) {
    React.render(
        <Error message={message} />,
        document.getElementById('errors')
    );
};
