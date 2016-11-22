(function() {
    'use strict';

    angular
        .module('21PointsApp')
        .config(bootstrapMaterialDesignConfig);

    //var compileServiceConfig;
    //compileServiceConfig.$inject = [];

    function bootstrapMaterialDesignConfig() {
        $.material.init();

    }
})();
