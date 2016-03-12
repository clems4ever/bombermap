exports.handleError = function(modulename, err) {
    if (err) {
        console.error(modulename, err.message);
        //setTimeout(start, 1000);
    }
}