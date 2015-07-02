
properties = {
    ssn: {
        clean: function (value) {
            if (value.length == 9) {
                return [true, value];
            } else {
                return [false, "invalid_length", "SSN should be 9 digits long"];
            }
        }
    },

    email: {
        clean: function (value) {
            return [value]
        }
    }
};
