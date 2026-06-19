// environments/environment.ts
export const environment = {
  production: false,

  // ===== API GATEWAY =====
  // Point d'entrée unique pour tous les services
  apiUrl: 'http://localhost:8080',

  // ===== PREFIXES DES SERVICES =====
  // Ces préfixes sont définis dans les routes du Gateway
  servicePrefixes: {
    user: '/api', // /api/students/**, /api/teachers/**
    course: '/api', // /api/courses/**
    payment: '/api', // /api/payments/**
    communication: '/api', // /api/messages/**
    school: '/api', // /api/schools/**
    assessment: '/api', // /api/assessments/**
    attendance: '/api', // /api/attendances/**
    timetable: '/api', // /api/timetables/**
  },

  // ===== AUTRES CONFIGURATIONS =====
  authEnabled: true,
  tokenKey: 'access_token',
  userKey: 'user',

  // ===== POUR LE DÉVELOPPEMENT =====
  logLevel: 'debug',

  // ===== EN PRODUCTION (à décommenter) =====
  // apiUrl: 'https://api.mondomaine.com',
};
