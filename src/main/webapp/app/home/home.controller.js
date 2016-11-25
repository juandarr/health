(function() {
    'use strict';

    angular
        .module('21PointsApp')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['$scope', 'Principal', 'Points', 'Preferences', 'LoginService', '$state'];

    function HomeController ($scope, Principal, Points, Preferences, LoginService, $state) {
        var vm = this;

        vm.account = null;
        vm.isAuthenticated = null;
        vm.login = LoginService.open;
        vm.register = register;

        $scope.$on('authenticationSuccess', function() {
            getAccount();
        });

        getAccount();

        function getAccount() {
            Principal.identity().then(function(account) {
                vm.account = account;
                vm.isAuthenticated = Principal.isAuthenticated;
            if (vm.isAuthenticated()){
                Preferences.user(function (preferences) {
                    vm.preferences = preferences;
                    Points.thisWeek(function (points) {
                        vm.pointsThisWeek = points;
                        vm.pointsPercentage = (points.points /vm.preferences.weekly_goal ) * 100;
                    });
                });
            }
            });
        }

        function register () {
            $state.go('register');
        }
    }
})();
