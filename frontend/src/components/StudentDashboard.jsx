import React, { useState, useEffect } from 'react';
import { apiService } from '../services/api';
import { StatCard } from './StatCard';
import { 
  GraduationCap, 
  Award, 
  TrendingUp, 
  BookOpen, 
  AlertTriangle, 
  Upload, 
  CheckCircle2, 
  Clock, 
  XCircle, 
  User, 
  Bell, 
  Download, 
  Sparkles, 
  Send,
  FileText,
  Check
} from 'lucide-react';

export const StudentDashboard = ({ activeTab, setActiveTab }) => {
  const [homeData, setHomeData] = useState(null);
  const [profile, setProfile] = useState(null);
  const [marksSummary, setMarksSummary] = useState(null);
  const [internalAnalysis, setInternalAnalysis] = useState(null);
  const [semComparison, setSemComparison] = useState(null);
  const [certificates, setCertificates] = useState([]);
  const [riskAlerts, setRiskAlerts] = useState([]);
  const [notifications, setNotifications] = useState([]);
  const [report, setReport] = useState(null);
  const [loading, setLoading] = useState(true);

  // Profile Edit State
  const [editProfile, setEditProfile] = useState({ phone: '', gender: '', dateOfBirth: '' });
  const [profileMsg, setProfileMsg] = useState('');

  // Certificate Form State
  const [newCert, setNewCert] = useState({ title: '', category: 'Online Course', issueOrganization: '', fileUrl: '' });
  const [certMsg, setCertMsg] = useState('');

  // AI Chat State
  const [chatPrompt, setChatPrompt] = useState('');
  const [chatHistory, setChatHistory] = useState([]);
  const [aiLoading, setAiLoading] = useState(false);

  const loadStudentData = async () => {
    setLoading(true);
    try {
      const [
        hData,
        pData,
        mSummary,
        iaData,
        compData,
        certData,
        riskData,
        notifData,
        repData,
        aiHist
      ] = await Promise.all([
        apiService.getStudentHome().catch(() => null),
        apiService.getStudentProfile().catch(() => null),
        apiService.getStudentMarksSummary().catch(() => null),
        apiService.getInternalMarkAnalysis().catch(() => null),
        apiService.compareSemesters().catch(() => null),
        apiService.getMyCertificates().catch(() => []),
        apiService.getMyRiskAlerts().catch(() => []),
        apiService.getStudentNotifications().catch(() => []),
        apiService.getAcademicReport().catch(() => null),
        apiService.getAiHistory().catch(() => [])
      ]);

      setHomeData(hData);
      setProfile(pData);
      if (pData) {
        setEditProfile({
          phone: pData.phone || '',
          gender: pData.gender || 'Male',
          dateOfBirth: pData.dateOfBirth || ''
        });
      }
      setMarksSummary(mSummary);
      setInternalAnalysis(iaData);
      setSemComparison(compData);
      setCertificates(certData);
      setRiskAlerts(riskData);
      setNotifications(notifData);
      setReport(repData);
      setChatHistory(aiHist);
    } catch (err) {
      console.error('Error loading student data:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadStudentData();
  }, []);

  const handleUpdateProfile = async (e) => {
    e.preventDefault();
    setProfileMsg('');
    try {
      const updated = await apiService.updateStudentProfile(editProfile);
      setProfile(updated);
      setProfileMsg('✅ Profile updated successfully!');
      setTimeout(() => setProfileMsg(''), 4000);
    } catch (err) {
      setProfileMsg('❌ Failed to update profile: ' + err.message);
    }
  };

  const handleUploadCert = async (e) => {
    e.preventDefault();
    setCertMsg('');
    try {
      await apiService.uploadCertificate(newCert);
      setNewCert({ title: '', category: 'Online Course', issueOrganization: '', fileUrl: '' });
      setCertMsg('✅ Certificate submitted for faculty verification!');
      const updatedCerts = await apiService.getMyCertificates();
      setCertificates(updatedCerts);
      setTimeout(() => setCertMsg(''), 4000);
    } catch (err) {
      setCertMsg('❌ Certificate upload failed: ' + err.message);
    }
  };

  const handleMarkNotificationRead = async (id) => {
    try {
      await apiService.markNotificationRead(id);
      setNotifications(prev => prev.map(n => n.id === id ? { ...n, read: true } : n));
    } catch (err) {
      console.error('Error marking notification read:', err);
    }
  };

  const handleDownloadPdf = async () => {
    try {
      await apiService.downloadAcademicReportPdf();
    } catch (err) {
      alert('Failed to download PDF report: ' + err.message);
    }
  };

  const handleSendAiPrompt = async (promptText) => {
    const text = promptText || chatPrompt;
    if (!text.trim()) return;
    setAiLoading(true);
    try {
      const resp = await apiService.askAi(text);
      setChatHistory(prev => [...prev, resp]);
      setChatPrompt('');
    } catch (err) {
      console.error('AI chat error:', err);
    } finally {
      setAiLoading(false);
    }
  };

  if (loading) {
    return <div style={{ padding: '3rem', textAlign: 'center', color: 'var(--text-muted)' }}>Loading Student Hub...</div>;
  }

  return (
    <div className="page-wrapper">
      {/* HOME TAB: EVERYTHING INTEGRATED IN ONE HOME PAGE */}
      {(activeTab === 'dashboard' || !activeTab) && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem' }}>
          {/* Top Welcome Header & Quick PDF Download */}
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <h2 className="page-title">Welcome Back, {profile?.fullName || 'Student'}! 👋</h2>
              <p className="page-subtitle">Register No: {profile?.registerNumber} | {profile?.departmentName} ({profile?.className})</p>
            </div>
            <button onClick={handleDownloadPdf} className="btn-primary" style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <Download size={18} /> Download Academic PDF Report
            </button>
          </div>

          {/* Academic Advisory Warning Banner */}
          {riskAlerts.length > 0 && (
            <div style={{
              background: '#FEF2F2',
              border: '1px solid #FCA5A5',
              borderRadius: 'var(--radius-lg)',
              padding: '1.25rem 1.5rem',
              display: 'flex',
              alignItems: 'flex-start',
              gap: '1rem'
            }}>
              <AlertTriangle color="#EF4444" size={24} style={{ marginTop: '2px' }} />
              <div>
                <h4 style={{ color: '#EF4444', fontWeight: 800, fontSize: '1rem', marginBottom: '0.25rem' }}>
                  Academic Advisory Alert Triggered
                </h4>
                {riskAlerts.map(alert => (
                  <div key={alert.id} style={{ fontSize: '0.88rem', color: '#0F172A', marginTop: '0.35rem' }}>
                    • <strong>Reason:</strong> {alert.reason} | <strong style={{ color: '#10B981' }}>Recommendation:</strong> {alert.recommendedAction}
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Top Metric Cards */}
          <div className="grid-4">
            <StatCard
              title="Cumulative CGPA"
              value={profile?.cgpa ? profile.cgpa.toFixed(2) : '0.00'}
              subtext="Source of Truth (PostgreSQL)"
              icon={GraduationCap}
              color={profile?.cgpa >= 8.0 ? 'emerald' : profile?.cgpa >= 6.5 ? 'amber' : 'rose'}
            />
            <StatCard
              title="Current Semester GPA"
              value={homeData?.currentGpa ? homeData.currentGpa.toFixed(2) : '0.00'}
              subtext={`Semester ${profile?.currentSemester || 1} Performance`}
              icon={TrendingUp}
              color="cyan"
            />
            <StatCard
              title="Verified Certificates"
              value={certificates.filter(c => c.status === 'APPROVED').length}
              subtext={`Out of ${certificates.length} submitted`}
              icon={Award}
              color="primary"
            />
            <StatCard
              title="Unread Notifications"
              value={notifications.filter(n => !n.read).length}
              subtext="Smart Alerts & System Notices"
              icon={Bell}
              color="indigo"
            />
          </div>

          {/* Section 1: Recent Activity & AI Suggestions */}
          <div className="grid-2">
            <div className="glass-card">
              <h3 style={{ fontSize: '1.1rem', fontWeight: 700, color: '#0F172A', marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <Clock color="#2563EB" size={20} /> Recent Academic Activity
              </h3>
              <ul style={{ listStyle: 'none', padding: 0, margin: 0 }}>
                {homeData?.recentActivity?.map((act, idx) => (
                  <li key={idx} style={{ padding: '0.75rem 0', borderBottom: '1px solid #F1F5F9', fontSize: '0.9rem', color: '#334155' }}>
                    {act}
                  </li>
                ))}
              </ul>
            </div>

            <div className="glass-card" style={{ background: 'linear-gradient(135deg, #EFF6FF 0%, #FFFFFF 100%)' }}>
              <h3 style={{ fontSize: '1.1rem', fontWeight: 700, color: '#1E40AF', marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <Sparkles color="#2563EB" size={20} /> AI Academic Suggestions
              </h3>
              <ul style={{ listStyle: 'none', padding: 0, margin: 0 }}>
                {homeData?.aiSuggestions?.map((sug, idx) => (
                  <li key={idx} style={{ padding: '0.75rem 0', borderBottom: '1px solid #DBEAFE', fontSize: '0.9rem', color: '#1E3A8A', fontWeight: 500 }}>
                    💡 {sug}
                  </li>
                ))}
              </ul>
              <div style={{ marginTop: '1.25rem', paddingTop: '1rem', borderTop: '1px solid #BFDBFE' }}>
                <div style={{ fontSize: '0.8rem', fontWeight: 700, color: '#1E40AF', marginBottom: '0.5rem', textTransform: 'uppercase' }}>
                  Ask AI Router Quick Prompts:
                </div>
                <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem' }}>
                  {["What is my CGPA?", "Show internal mark analysis", "Which subject am I weak in?", "Is my certificate approved?"].map((qp, i) => (
                    <button
                      key={i}
                      onClick={() => {
                        setActiveTab('ai-advisor');
                        handleSendAiPrompt(qp);
                      }}
                      style={{
                        padding: '0.35rem 0.75rem',
                        fontSize: '0.78rem',
                        borderRadius: '999px',
                        background: '#FFFFFF',
                        border: '1px solid #93C5FD',
                        color: '#2563EB',
                        cursor: 'pointer',
                        fontWeight: 600
                      }}
                    >
                      🤖 "{qp}"
                    </button>
                  ))}
                </div>
              </div>
            </div>
          </div>

          {/* Section 2: Internal Mark Analysis & Trends */}
          <div className="glass-card">
            <h3 style={{ fontSize: '1.18rem', fontWeight: 700, color: '#0F172A', marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <BookOpen color="#4F46E5" size={22} /> Internal Mark Analysis & Performance Trends
            </h3>
            <div className="table-container">
              <table className="custom-table">
                <thead>
                  <tr>
                    <th>Subject</th>
                    <th>Highest Mark</th>
                    <th>Lowest Mark</th>
                    <th>Average Score</th>
                    <th>Latest Test</th>
                    <th>Performance Trend</th>
                  </tr>
                </thead>
                <tbody>
                  {internalAnalysis?.subjectAnalyses?.map(sa => (
                    <tr key={sa.subjectId}>
                      <td style={{ fontWeight: 600 }}>{sa.subjectName} ({sa.subjectCode})</td>
                      <td style={{ color: '#10B981', fontWeight: 700 }}>{sa.highestMark}</td>
                      <td style={{ color: '#EF4444' }}>{sa.lowestMark}</td>
                      <td style={{ fontWeight: 700 }}>{sa.averageMark}</td>
                      <td style={{ fontWeight: 800, color: '#2563EB' }}>{sa.latestMark}</td>
                      <td>
                        <span className={`badge ${sa.trend === 'IMPROVING' ? 'badge-success' : sa.trend === 'DECLINING' ? 'badge-danger' : 'badge-warning'}`}>
                          {sa.trend} ({sa.markDifference > 0 ? `+${sa.markDifference}` : sa.markDifference})
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>

          {/* Section 3: Final Semester Exam Grades */}
          <div className="glass-card">
            <h3 style={{ fontSize: '1.1rem', fontWeight: 700, color: '#0F172A', marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <GraduationCap color="#10B981" size={20} /> Final Semester Exam Grades
            </h3>
            <div className="table-container">
              <table className="custom-table">
                <thead>
                  <tr>
                    <th>Subject</th>
                    <th>Semester</th>
                    <th>Grade</th>
                    <th>Grade Points</th>
                  </tr>
                </thead>
                <tbody>
                  {marksSummary?.semesterMarks?.map(sm => (
                    <tr key={sm.id}>
                      <td style={{ fontWeight: 600 }}>{sm.subject?.name}</td>
                      <td>Sem {sm.semester}</td>
                      <td><span className="badge badge-success">{sm.letterGrade}</span></td>
                      <td style={{ fontWeight: 700 }}>{sm.gradePoints}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>

          {/* Section 4: Semester Progression & Analytics */}
          <div className="glass-card">
            <h3 style={{ fontSize: '1.18rem', fontWeight: 700, color: '#0F172A', marginBottom: '1.25rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <TrendingUp color="#2563EB" size={22} /> Semester GPA Progression & Comparison
            </h3>

            <div className="grid-4" style={{ marginBottom: '1rem' }}>
              {semComparison?.semesterGpas?.map(item => (
                <div key={item.semester} className="glass-card-static" style={{ background: '#F8FAFC', border: '1px solid #E2E8F0', textAlign: 'center' }}>
                  <div style={{ fontSize: '0.8rem', color: '#64748B', fontWeight: 600 }}>SEMESTER {item.semester}</div>
                  <div style={{ fontSize: '2rem', fontWeight: 800, color: '#2563EB', margin: '0.35rem 0' }}>
                    {item.gpa.toFixed(2)}
                  </div>
                  <span className={`badge ${item.performanceTrend === 'IMPROVED' ? 'badge-success' : item.performanceTrend === 'DECLINED' ? 'badge-danger' : 'badge-warning'}`}>
                    {item.performanceTrend}
                  </span>
                </div>
              ))}
            </div>
          </div>

          {/* Section 5: Strengths & Weaknesses Breakdown */}
          <div className="grid-2">
            <div className="glass-card">
              <h4 style={{ color: '#166534', fontWeight: 700, marginBottom: '0.75rem' }}>🌟 Top Academic Strengths</h4>
              <ul style={{ listStyle: 'none', padding: 0 }}>
                {report?.strengths?.map((st, i) => (
                  <li key={i} style={{ padding: '0.5rem 0', borderBottom: '1px solid #F1F5F9' }}>
                    <strong>{st.subjectName} ({st.subjectCode})</strong> — Avg: {st.averageMark}% | Grade: <span className="badge badge-success">{st.grade}</span>
                  </li>
                ))}
              </ul>
            </div>
            <div className="glass-card">
              <h4 style={{ color: '#991B1B', fontWeight: 700, marginBottom: '0.75rem' }}>⚠️ Weak Subjects Requiring Attention</h4>
              <ul style={{ listStyle: 'none', padding: 0 }}>
                {report?.weaknesses?.length > 0 ? report.weaknesses.map((wk, i) => (
                  <li key={i} style={{ padding: '0.5rem 0', borderBottom: '1px solid #F1F5F9' }}>
                    <strong>{wk.subjectName} ({wk.subjectCode})</strong> — Avg: {wk.averageMark}% | Grade: <span className="badge badge-danger">{wk.grade}</span>
                  </li>
                )) : <div style={{ color: '#64748B', fontSize: '0.9rem' }}>No subjects marked as weak!</div>}
              </ul>
            </div>
          </div>

          {/* Section 6: Official Academic Report Preview & Download */}
          <div className="glass-card" style={{ border: '2px solid #BFDBFE' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.25rem' }}>
              <div>
                <h3 style={{ fontSize: '1.18rem', fontWeight: 700, color: '#0F172A', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                  <FileText color="#2563EB" size={22} /> Official Student Academic Report & PDF Export
                </h3>
                <p style={{ fontSize: '0.88rem', color: '#64748B', margin: 0 }}>Verified academic credentials, semester GPA history, mark trends, and AI recommendations.</p>
              </div>
              <button onClick={handleDownloadPdf} className="btn-primary" style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <Download size={18} /> Download Official PDF Report
              </button>
            </div>

            <div style={{ background: '#F8FAFC', padding: '1.25rem', borderRadius: 'var(--radius-md)', border: '1px solid #E2E8F0', marginBottom: '1rem' }}>
              <div className="grid-3">
                <div><strong>Student Name:</strong> {report?.profile?.fullName}</div>
                <div><strong>Register No:</strong> {report?.profile?.registerNumber}</div>
                <div><strong>Department:</strong> {report?.profile?.departmentName}</div>
                <div><strong>Class:</strong> {report?.profile?.className}</div>
                <div><strong>Current Semester:</strong> Semester {report?.profile?.currentSemester}</div>
                <div><strong>CGPA:</strong> <span style={{ color: '#2563EB', fontWeight: 800 }}>{report?.profile?.cgpa?.toFixed(2)}</span></div>
              </div>
            </div>

            <h4 style={{ fontWeight: 700, color: '#1E293B', marginBottom: '0.5rem' }}>AI Recommendations Summary</h4>
            <ul style={{ listStyle: 'none', padding: 0, margin: 0 }}>
              {report?.aiRecommendations?.map((rec, i) => (
                <li key={i} style={{ padding: '0.6rem 0.85rem', background: '#EFF6FF', borderRadius: 'var(--radius-md)', marginBottom: '0.5rem', fontSize: '0.88rem', color: '#1E40AF', fontWeight: 500 }}>
                  🤖 {rec}
                </li>
              ))}
            </ul>
          </div>

        </div>
      )}

      {/* MY PROFILE TAB */}
      {activeTab === 'profile' && (
        <div className="grid-2">
          <div className="glass-card">
            <h3 style={{ fontSize: '1.1rem', fontWeight: 700, color: '#0F172A', marginBottom: '1.25rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <User color="#2563EB" size={20} /> Academic Profile Details
            </h3>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.85rem', fontSize: '0.92rem' }}>
              <div><strong style={{ color: '#64748B' }}>Full Name:</strong> <span style={{ fontWeight: 700 }}>{profile?.fullName}</span></div>
              <div><strong style={{ color: '#64748B' }}>Register Number:</strong> <span style={{ fontWeight: 700 }}>{profile?.registerNumber}</span></div>
              <div><strong style={{ color: '#64748B' }}>Email:</strong> {profile?.email}</div>
              <div><strong style={{ color: '#64748B' }}>Department:</strong> {profile?.departmentName}</div>
              <div><strong style={{ color: '#64748B' }}>Class:</strong> {profile?.className}</div>
              <div><strong style={{ color: '#64748B' }}>Current Semester:</strong> Semester {profile?.currentSemester}</div>
              <div><strong style={{ color: '#64748B' }}>Academic Year:</strong> {profile?.academicYear}</div>
              <div><strong style={{ color: '#64748B' }}>Cumulative CGPA:</strong> <span style={{ color: '#2563EB', fontWeight: 800 }}>{profile?.cgpa?.toFixed(2)}</span></div>
            </div>
            <div style={{ marginTop: '1.5rem', padding: '1rem', background: '#F8FAFC', borderRadius: 'var(--radius-md)', fontSize: '0.82rem', color: '#64748B' }}>
              🔒 <strong>Security Policy:</strong> Academic metrics (CGPA, GPA, Class, Department) are verified by faculty/administration and cannot be altered directly.
            </div>
          </div>

          <div className="glass-card">
            <h3 style={{ fontSize: '1.1rem', fontWeight: 700, color: '#0F172A', marginBottom: '1.25rem' }}>Update Personal Information</h3>
            {profileMsg && <div style={{ marginBottom: '1rem', padding: '0.75rem', borderRadius: 'var(--radius-md)', background: '#F0FDF4', color: '#166534', fontWeight: 600 }}>{profileMsg}</div>}
            <form onSubmit={handleUpdateProfile}>
              <div className="form-group">
                <label className="form-label">Contact Phone Number</label>
                <input className="form-input" value={editProfile.phone} onChange={e => setEditProfile({ ...editProfile, phone: e.target.value })} placeholder="+91 9876543210" required />
              </div>
              <div className="form-group">
                <label className="form-label">Gender</label>
                <select className="form-select" value={editProfile.gender} onChange={e => setEditProfile({ ...editProfile, gender: e.target.value })}>
                  <option value="Male">Male</option>
                  <option value="Female">Female</option>
                  <option value="Other">Other</option>
                </select>
              </div>
              <div className="form-group">
                <label className="form-label">Date of Birth</label>
                <input type="date" className="form-input" value={editProfile.dateOfBirth} onChange={e => setEditProfile({ ...editProfile, dateOfBirth: e.target.value })} required />
              </div>
              <button type="submit" className="btn-primary" style={{ width: '100%' }}>
                Save Profile Updates
              </button>
            </form>
          </div>
        </div>
      )}

      {/* CERTIFICATES TAB */}
      {activeTab === 'my-certificates' && (
        <div className="grid-2">
          <div className="glass-card">
            <h3 style={{ fontSize: '1.1rem', fontWeight: 700, color: '#0F172A', marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <Upload color="#06B6D4" size={20} /> Upload New Certificate
            </h3>
            {certMsg && <div style={{ marginBottom: '1rem', padding: '0.75rem', borderRadius: 'var(--radius-md)', background: '#F0FDF4', color: '#166534', fontWeight: 600 }}>{certMsg}</div>}
            <form onSubmit={handleUploadCert}>
              <div className="form-group">
                <label className="form-label">Certificate Title</label>
                <input className="form-input" placeholder="e.g. AWS Certified Cloud Practitioner" value={newCert.title} onChange={e => setNewCert({ ...newCert, title: e.target.value })} required />
              </div>
              <div className="grid-2">
                <div className="form-group">
                  <label className="form-label">Category</label>
                  <select className="form-select" value={newCert.category} onChange={e => setNewCert({ ...newCert, category: e.target.value })}>
                    <option value="Online Course">Online Course (NPTEL / Coursera)</option>
                    <option value="Hackathon">Hackathon / Competition</option>
                    <option value="Paper Publication">Paper Publication</option>
                    <option value="Workshop">Workshop / Seminar</option>
                  </select>
                </div>
                <div className="form-group">
                  <label className="form-label">Issuing Organization</label>
                  <input className="form-input" placeholder="e.g. IIT Madras" value={newCert.issueOrganization} onChange={e => setNewCert({ ...newCert, issueOrganization: e.target.value })} required />
                </div>
              </div>
              <button type="submit" className="btn-primary" style={{ width: '100%' }}>
                <Upload size={16} /> Submit Certificate for Verification
              </button>
            </form>
          </div>

          <div className="glass-card">
            <h3 style={{ fontSize: '1.1rem', fontWeight: 700, color: '#0F172A', marginBottom: '1rem' }}>Uploaded Certificates & Status</h3>
            <div className="table-container">
              <table className="custom-table">
                <thead>
                  <tr>
                    <th>Title</th>
                    <th>Category</th>
                    <th>Status</th>
                    <th>Reviewer Feedback</th>
                  </tr>
                </thead>
                <tbody>
                  {certificates.map(cert => (
                    <tr key={cert.id}>
                      <td style={{ fontWeight: 600 }}>{cert.title}</td>
                      <td><span className="badge badge-info">{cert.category}</span></td>
                      <td>
                        <span className={`badge ${cert.status === 'APPROVED' ? 'badge-success' : cert.status === 'REJECTED' ? 'badge-danger' : 'badge-warning'}`}>
                          {cert.status === 'APPROVED' && <CheckCircle2 size={12} />}
                          {cert.status === 'PENDING' && <Clock size={12} />}
                          {cert.status === 'REJECTED' && <XCircle size={12} />}
                          {cert.status}
                        </span>
                      </td>
                      <td style={{ fontSize: '0.82rem', color: '#64748B' }}>{cert.reviewerComments || 'Pending faculty review'}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      )}

      {/* NOTIFICATIONS TAB */}
      {activeTab === 'notifications' && (
        <div className="glass-card">
          <h3 style={{ fontSize: '1.1rem', fontWeight: 700, color: '#0F172A', marginBottom: '1.25rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <Bell color="#2563EB" size={20} /> Smart Notifications & Academic Alerts
          </h3>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            {notifications.length === 0 ? (
              <div style={{ padding: '2rem', textAlign: 'center', color: '#64748B' }}>No notifications found.</div>
            ) : notifications.map(notif => (
              <div key={notif.id} style={{
                padding: '1rem 1.25rem',
                borderRadius: 'var(--radius-md)',
                background: notif.read ? '#F8FAFC' : '#EFF6FF',
                border: notif.read ? '1px solid #E2E8F0' : '1px solid #BFDBFE',
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center'
              }}>
                <div>
                  <div style={{ fontWeight: 700, color: '#0F172A', fontSize: '0.95rem' }}>{notif.title}</div>
                  <div style={{ fontSize: '0.88rem', color: '#475569', marginTop: '0.25rem' }}>{notif.message}</div>
                  <div style={{ fontSize: '0.75rem', color: '#94A3B8', marginTop: '0.35rem' }}>
                    {new Date(notif.createdAt).toLocaleString()}
                  </div>
                </div>
                {!notif.read && (
                  <button onClick={() => handleMarkNotificationRead(notif.id)} className="btn-secondary" style={{ padding: '0.4rem 0.85rem', fontSize: '0.8rem', display: 'flex', alignItems: 'center', gap: '0.35rem' }}>
                    <Check size={14} /> Mark as Read
                  </button>
                )}
              </div>
            ))}
          </div>
        </div>
      )}

      {/* AI ACADEMIC ADVISOR TAB */}
      {activeTab === 'ai-advisor' && (
        <div className="glass-card" style={{ display: 'flex', flexDirection: 'column', height: '600px' }}>
          <div style={{ paddingBottom: '1rem', borderBottom: '1px solid #E2E8F0', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <Sparkles color="#2563EB" size={22} />
            <div>
              <h3 style={{ fontSize: '1.1rem', fontWeight: 700, color: '#0F172A', margin: 0 }}>EduSphere AI Academic Intelligence Router</h3>
              <p style={{ fontSize: '0.8rem', color: '#64748B', margin: 0 }}>Conversational assistant backed by verified PostgreSQL academic data</p>
            </div>
          </div>

          <div style={{ flex: 1, overflowY: 'auto', padding: '1rem 0', display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            {chatHistory.map((msg, idx) => (
              <React.Fragment key={msg.id || idx}>
                <div style={{ alignSelf: 'flex-end', background: '#2563EB', color: '#FFFFFF', padding: '0.75rem 1rem', borderRadius: '1rem 1rem 0 1rem', maxWidth: '75%', fontSize: '0.9rem' }}>
                  {msg.prompt}
                </div>
                <div style={{ alignSelf: 'flex-start', background: '#F1F5F9', color: '#0F172A', padding: '0.85rem 1.1rem', borderRadius: '1rem 1rem 1rem 0', maxWidth: '80%', fontSize: '0.9rem', whiteSpace: 'pre-line', lineHeight: '1.5' }}>
                  {msg.response}
                </div>
              </React.Fragment>
            ))}
            {aiLoading && (
              <div style={{ alignSelf: 'flex-start', background: '#F1F5F9', padding: '0.75rem 1rem', borderRadius: '1rem', fontSize: '0.85rem', color: '#64748B' }}>
                🤖 Querying EduSphere Academic Data Agent...
              </div>
            )}
          </div>

          <div style={{ paddingTop: '1rem', borderTop: '1px solid #E2E8F0', display: 'flex', gap: '0.75rem' }}>
            <input
              className="form-input"
              value={chatPrompt}
              onChange={e => setChatPrompt(e.target.value)}
              onKeyDown={e => e.key === 'Enter' && handleSendAiPrompt()}
              placeholder="Ask AI Router: 'What is my CGPA?', 'Compare my last two semesters', etc."
            />
            <button onClick={() => handleSendAiPrompt()} className="btn-primary" disabled={aiLoading} style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <Send size={16} /> Send
            </button>
          </div>
        </div>
      )}
    </div>
  );
};
