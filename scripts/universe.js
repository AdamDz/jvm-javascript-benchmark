
star = {
    ssn: {
        clean: function (value) {
            return [value.length == 9, "invalid_length", "SSN should be 9 digits long"]
        }
    }
};
