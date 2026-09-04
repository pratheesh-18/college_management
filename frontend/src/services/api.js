const API_BASE_URL = '/api';

export const getAuthToken = () => localStorage.getItem('edusphere_token');

export const apiFetch = async (endpoint, options = {}) => {
  const token = getAuthToken();
  const headers = {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
    ...options.headers
  };

  const response = await fetch(`${API_BASE_URL}${endpoint}`, {
    ...options,
    headers
  });

  if (!response.ok) {
    const errorData = await response.json().catch(() => ({}));
    throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
  }

  return response.json();
};

export const apiService = {
  // Auth
  login: (data) => apiFetch('/auth/login', { method: 'POST', body: JSON.stringify(data) }),
  register: (data) => apiFetch('/auth/register', { method: 'POST', body: JSON.stringify(data) }),
  getCurrentUser: () => apiFetch('/auth/me'),

  // Admin
  getAnalytics: () => apiFetch('/admin/analytics'),
  getDepartments: () => apiFetch('/admin/departments'),
  createDepartment: (data) => apiFetch('/admin/departments', { method: 'POST', body: JSON.stringify(data) }),
  getAcademicYears: () => apiFetch('/admin/academic-years'),
  createAcademicYear: (data) => apiFetch('/admin/academic-years', { method: 'POST', body: JSON.stringify(data) }),
  getClasses: () => apiFetch('/admin/classes'),
  createClass: (data) => apiFetch('/admin/classes', { method: 'POST', body: JSON.stringify(data) }),
  getSubjects: () => apiFetch('/admin/subjects'),
  createSubject: (data) => apiFetch('/admin/subjects', { method: 'POST', body: JSON.stringify(data) }),
  getFaculty: () => apiFetch('/admin/faculty'),
  getAllocations: () => apiFetch('/admin/allocations'),
  allocateFaculty: (facultyId, subjectId, classId) => 
    apiFetch(`/admin/allocations?facultyId=${facultyId}&subjectId=${subjectId}&classId=${classId}`, { method: 'POST' }),
  assignTutor: (classId, facultyId) =>
    apiFetch(`/admin/tutor/assign?classId=${classId}&facultyId=${facultyId}`, { method: 'POST' }),
  assignAdvisor: (classId, facultyId) =>
    apiFetch(`/admin/advisor/assign?classId=${classId}&facultyId=${facultyId}`, { method: 'POST' }),
  promoteStudent: (studentId) => apiFetch(`/admin/students/${studentId}/promote`, { method: 'POST' }),

  // Faculty
  getAssignedSubjects: () => apiFetch('/faculty/assigned-subjects'),
  getStudentsInClass: (classId) => apiFetch(`/faculty/classes/${classId}/students`),
  saveInternalMark: (data) => apiFetch('/faculty/marks/internal', { method: 'POST', body: JSON.stringify(data) }),
  saveSemesterMark: (data) => apiFetch('/faculty/marks/semester', { method: 'POST', body: JSON.stringify(data) }),

  // Student
  getStudentMarksSummary: () => apiFetch('/student/marks/summary'),
  compareSemesters: () => apiFetch('/student/performance/compare'),

  // Certificates
  getMyCertificates: () => apiFetch('/certificates/my'),
  uploadCertificate: (data) => apiFetch('/certificates/upload', { method: 'POST', body: JSON.stringify(data) }),
  getAllCertificates: () => apiFetch('/certificates/all'),
  verifyCertificate: (id, status, comments) => 
    apiFetch(`/certificates/${id}/verify?status=${status}&comments=${encodeURIComponent(comments || '')}`, { method: 'POST' }),

  // Risk Engine
  getAllRiskAlerts: () => apiFetch('/risk/alerts/all'),
  getMyRiskAlerts: () => apiFetch('/risk/alerts/my'),
  resolveRiskAlert: (id) => apiFetch(`/risk/alerts/${id}/resolve`, { method: 'POST' }),

  // AI Assistant
  getAiHistory: () => apiFetch('/ai/history'),
  askAi: (prompt) => apiFetch('/ai/chat', { method: 'POST', body: JSON.stringify({ prompt }) })
};
