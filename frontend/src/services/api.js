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
  getStudentHome: () => apiFetch('/v1/student/home'),
  getStudentProfile: () => apiFetch('/v1/student/me/profile'),
  updateStudentProfile: (data) => apiFetch('/v1/student/me/profile', { method: 'PUT', body: JSON.stringify(data) }),
  getAcademicOverview: () => apiFetch('/v1/student/me/academic-overview'),
  getAcademicHistory: () => apiFetch('/v1/student/me/academic-history'),
  getInternalMarkAnalysis: () => apiFetch('/v1/student/me/internal-marks/analysis'),
  getSubjectPerformance: () => apiFetch('/v1/student/me/subjects/performance'),
  getStudentNotifications: () => apiFetch('/v1/student/notifications'),
  markNotificationRead: (id) => apiFetch(`/v1/student/notifications/${id}/read`, { method: 'PATCH' }),
  getAcademicReport: () => apiFetch('/v1/student/me/report'),
  downloadAcademicReportPdf: async () => {
    const token = getAuthToken();
    const response = await fetch('/api/v1/student/me/report/pdf', {
      headers: token ? { Authorization: `Bearer ${token}` } : {}
    });
    if (!response.ok) throw new Error('Failed to download PDF report');
    const blob = await response.blob();
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'Academic_Report.pdf';
    document.body.appendChild(a);
    a.click();
    a.remove();
  },
  getStudentMarksSummary: () => apiFetch('/student/marks/summary'),
  compareSemesters: () => apiFetch('/v1/student/me/performance/comparison'),

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
  getAiHistory: () => apiFetch('/v1/student/ai/history'),
  askAi: (prompt, conversationId) => apiFetch('/v1/student/ai/chat', { method: 'POST', body: JSON.stringify({ message: prompt, conversationId }) }),
  askStudentAi: (message, conversationId) => apiFetch('/v1/student/ai/chat', { method: 'POST', body: JSON.stringify({ message, conversationId }) })
};
