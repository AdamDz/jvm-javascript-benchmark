var MyError = Java.type('com.company.MyError');

function error(code, value) {
    return new MyError(code, value);
}

error;