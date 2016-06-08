(function() {
    'use strict';

    angular
        .module('jHealthApp')
        .controller('GoalDialogController', GoalDialogController);

    GoalDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Goal', 'User'];

    function GoalDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Goal, User) {
        var vm = this;

        vm.goal = entity;
        vm.clear = clear;
        vm.save = save;
        vm.users = User.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.goal.id !== null) {
                Goal.update(vm.goal, onSaveSuccess, onSaveError);
            } else {
                Goal.save(vm.goal, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('jHealthApp:goalUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
