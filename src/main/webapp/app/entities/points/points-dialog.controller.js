(function() {
    'use strict';

    angular
        .module('21PointsApp')
        .controller('PointsDialogController', PointsDialogController);

    PointsDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Points', 'User'];

    function PointsDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Points, User) {
        var vm = this;
        vm.points = entity;
        if (vm.points.id == null) {
            $scope.vm.points.date = new Date();
        }
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.users = User.query();

        $timeout(function (){
            angular.element('.form-group:eq(0)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.points.id !== null) {
                Points.update(vm.points, onSaveSuccess, onSaveError);
            } else {
                Points.save(vm.points, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('21PointsApp:pointsUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.date = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
