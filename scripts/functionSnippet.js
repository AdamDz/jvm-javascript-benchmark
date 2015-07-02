function(value, error) {
    if (value.length != 9) {
        return error("invalid_length", "SSN must have 9 digits");
    } else {
        return value;
    }
}


