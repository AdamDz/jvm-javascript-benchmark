
properties = {
    ssn: {
        clean: function (value) {
            if (value.length == 9) {
                return [true];
            } else {
                return [false, "invalid_length", "SSN should be 9 digits long"];
            }
        }
    }
};
