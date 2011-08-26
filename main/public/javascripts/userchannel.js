
var UserChannel = function (spec) {
  spec.channel_name = spec.user.user_id + "_channel";
  var that = Channel(spec),
    my = {};
  my.types = {
    NEW_POWER: "newpower"
  };
  
  
  that.bindNewPower = function (f) {
    that.bind(my.types.NEW_POWER, function (power) {
      f(power);
    });
  };
  
  return that;
};