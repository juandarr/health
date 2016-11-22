(function() {
    'use strict';

    angular
        .module('21PointsApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('points', {
            parent: 'entity',
            url: '/points',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Points'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/points/points.html',
                    controller: 'PointsController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('points-detail', {
            parent: 'entity',
            url: '/points/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Points'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/points/points-detail.html',
                    controller: 'PointsDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Points', function($stateParams, Points) {
                    return Points.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'points',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('points-detail.edit', {
            parent: 'points-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/points/points-dialog.html',
                    controller: 'PointsDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Points', function(Points) {
                            return Points.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('points.new', {
            parent: 'points',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/points/points-dialog.html',
                    controller: 'PointsDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                date: null,
                                exercise: false,
                                meals: null,
                                sleep: false,
                                lesson: null,
                                overall: null,
                                notes: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('points', null, { reload: 'points' });
                }, function() {
                    $state.go('points');
                });
            }]
        })
        .state('points.edit', {
            parent: 'points',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/points/points-dialog.html',
                    controller: 'PointsDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Points', function(Points) {
                            return Points.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('points', null, { reload: 'points' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('points.delete', {
            parent: 'points',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/points/points-delete-dialog.html',
                    controller: 'PointsDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Points', function(Points) {
                            return Points.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('points', null, { reload: 'points' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
