import { createRouter, createWebHistory } from 'vue-router';
import Login from '@/views/Login.vue';

// 用户路由
const userRoutes = [
  {
    path: '/user',
    component: () => import('@/views/user/Layout.vue'),
    children: [
      {
        path: 'dashboard',
        component: () => import('@/views/user/Dashboard.vue')
      },
      {
        path: 'profile',
        component: () => import('@/views/user/Profile.vue')
      },
      {
        path: 'account',
        component: () => import('@/views/user/AccountManagement.vue')
      },
      {
        path: 'pets',
        component: () => import('@/views/user/Pets.vue')
      },
      {
        path: 'medical-records',
        component: () => import('@/views/user/MedicalRecords.vue')
      },
      {
        path: 'appointments',
        component: () => import('@/views/user/Appointments.vue')
      },
      {
        path: 'make-appointment',
        component: () => import('@/views/user/MakeAppointment.vue')
      }
    ]
  }
];

// 前台路由
const receptionistRoutes = [
  {
    path: '/receptionist',
    component: () => import('@/views/receptionist/Layout.vue'),
    children: [
      {
        path: 'dashboard',
        component: () => import('@/views/receptionist/Dashboard.vue')
      },
      {
        path: 'customers',
        component: () => import('@/views/receptionist/CustomerManagement.vue')
      },
      {
        path: 'pets',
        component: () => import('@/views/receptionist/PetManagement.vue')
      },
      {
        path: 'appointments',
        component: () => import('@/views/receptionist/AppointmentManagement.vue')
      },
      {
        path: 'billing',
        component: () => import('@/views/receptionist/BillingManagement.vue')
      },
      {
        path: 'inventory',
        component: () => import('@/views/receptionist/InventoryManagement.vue')
      },
      {
        path: 'queue',
        component: () => import('@/views/receptionist/QueueManagement.vue')
      }
    ]
  }
];

// 医生路由
const doctorRoutes = [
  {
    path: '/doctor',
    component: () => import('@/views/doctor/Layout.vue'),
    children: [
      {
        path: 'dashboard',
        component: () => import('@/views/doctor/Dashboard.vue')
      },
      {
        path: 'schedule',
        component: () => import('@/views/doctor/Schedule.vue')
      },
      {
        path: 'appointments',
        component: () => import('@/views/doctor/Appointments.vue')
      }
    ]
  }
];

// 院长路由
const directorRoutes = [
  {
    path: '/director',
    component: () => import('@/views/director/Layout.vue'),
    children: [
      {
        path: 'dashboard',
        component: () => import('@/views/director/Dashboard.vue')
      },
      {
        path: 'users',
        component: () => import('@/views/director/UserManagement.vue')
      },
      {
        path: 'customers',
        component: () => import('@/views/director/CustomerManagement.vue')
      },
      {
        path: 'schedules',
        component: () => import('@/views/director/DoctorSchedule.vue')
      },
      {
        path: 'inventory',
        component: () => import('@/views/director/InventoryManagement.vue')
      }
    ]
  }
];

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    component: Login
  },
  ...userRoutes,
  ...receptionistRoutes,
  ...doctorRoutes,
  ...directorRoutes
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token');
  const userRole = localStorage.getItem('userRole');
  
  // 如果访问登录页，直接放行
  if (to.path === '/login') {
    next();
    return;
  }
  
  // 如果没有token，跳转到登录页
  if (!token) {
    next('/login');
    return;
  }
  
  // 根据角色放行对应路由
  if (to.path.startsWith('/user') && userRole === 'USER') {
    next();
  } else if (to.path.startsWith('/receptionist') && userRole === 'RECEPTIONIST') {
    next();
  } else if (to.path.startsWith('/doctor') && userRole === 'DOCTOR') {
    next();
  } else if (to.path.startsWith('/director') && userRole === 'ADMIN') {
    next();
  } else {
    // 角色与访问路径不匹配，跳转到登录页
    next('/login');
  }
});

export default router;