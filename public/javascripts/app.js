// Start highlighting code
hljs.initHighlightingOnLoad();

// Handle main menu
YUI({
    classNamePrefix: 'pure'
}).use('gallery-sm-menu', function (Y) {

    var horizontalMenu = new Y.Menu({
        container         : '#menu-container',
        sourceNode        : '#menu-items',
        orientation       : 'horizontal',
        hideOnOutsideClick: false,
        hideOnClick       : false
    });

    horizontalMenu.render();
    horizontalMenu.show();
});
