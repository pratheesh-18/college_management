import React, { useState, useEffect } from 'react';
import { apiService } from '../services/api';
import { StatCard } from './StatCard';
import { GraduationCap, Award, TrendingUp, BookOpen, AlertTriangle, Upload, CheckCircle2, Clock, XCircle } from 'lucide-react';

export const StudentDashboard = ({ activeTab }) => {
  const [marksSummary, setMarksSummary] = useState(null);
  const [semComparison, setSemComparison] = useState(null);
  const [certificates, setCertificates] = useState([]);
  const [riskAlerts, setRiskAlerts] = useState([]);
  const [loading, setLoading] = useState(true);

  // Certificate Upload Form State
  const [newCert, setNewCert] = useState({ title: '', category: 'Online Course', issueOrganization: '', fileUrl: '' });

  const loadStudentData = async () => {
    setLoading(true);
    try {
      const [marksData, compData, certData, riskData] = await Promise.all([
        apiService.getStudentMarksSummary(),
        apiService.compareSemesters(),
        apiService.getMyCertificates(),
        apiService.getMyRiskAlerts()
      ]);
      setMarksSummary(marksData);
      setSemComparison(compData);
      setCertificates(certData);
      setRiskAlerts(riskData);
    } catch (err) {
      console.error('Error loading student data:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadStudentData();
  }, []);

  const handleUploadCert = async (e) => {
    e.preventDefault();
    await apiService.uploadCertificate(newCert);
    setNewCert({ title: '', category: 'Online Course', issueOrganization: '', fileUrl: '' });
    loadStudentData();
  };

  if (loading) {
    return <div style={{ padding: '3rem', textAlign: 'center', color: 'var(--text-muted)' }}>Loading Student Portal...</div>;
  }

  return (
    <div className="page-wrapper">
      <div style={{ marginBottom: '2rem' }}>
        <h2 className="page-title">Welcome Back, {marksSummary?.studentName || 'Student'}!</h2>
        <p className="page-subtitle">Register No: {marksSummary?.registerNumber} | Cumulative Performance Hub</p>
      </div>

      {/* Risk Alert Warning Banner */}
      {riskAlerts.length > 0 && (
        <div style={{
          background: '#FEF2F2',
          border: '1px solid #FCA5A5',
          borderRadius: 'var(--radius-lg)',
          padding: '1.25rem 1.5rem',
          marginBottom: '2rem',
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

      {/* Metric Cards */}
      <div className="grid-3" style={{ marginBottom: '2rem' }}>
        <StatCard
          title="Cumulative Grade Point Average"
          value={marksSummary?.cgpa ? marksSummary.cgpa.toFixed(2) : '0.00'}
          subtext="Target: 9.0+"
          icon={GraduationCap}
          color={marksSummary?.cgpa >= 8.0 ? 'emerald' : marksSummary?.cgpa >= 6.5 ? 'amber' : 'rose'}
        />
        <StatCard
          title="Current Semester GPA"
          value={marksSummary?.currentGpa ? marksSummary.currentGpa.toFixed(2) : '0.00'}
          subtext="Latest Semester Performance"
          icon={TrendingUp}
          color="cyan"
        />
        <StatCard
          title="Verified Certificates"
          value={certificates.filter(c => c.status === 'APPROVED').length}
          subtext={`Out of ${certificates.length} uploaded`}
          icon={Award}
          color="primary"
        />
      </div>

      {/* Semester Performance Comparison */}
      {(activeTab === 'dashboard' || activeTab === 'sem-compare') && (
        <div className="glass-card" style={{ marginBottom: '2rem' }}>
          <h3 style={{ fontSize: '1.18rem', fontWeight: 700, color: '#0F172A', marginBottom: '1.25rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <TrendingUp color="#2563EB" size={22} /> Semester GPA Progression & Trend Analysis
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
      )}

      {/* Internal & Semester Marks Breakdown */}
      {(activeTab === 'dashboard' || activeTab === 'my-marks') && (
        <div className="grid-2" style={{ marginBottom: '2rem' }}>
          <div className="glass-card">
            <h3 style={{ fontSize: '1.1rem', fontWeight: 700, color: '#0F172A', marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <BookOpen color="#4F46E5" size={20} /> Internal Marks
            </h3>
            <div className="table-container">
              <table className="custom-table">
                <thead>
                  <tr>
                    <th>Subject</th>
                    <th>Assessment Type</th>
                    <th>Score</th>
                    <th>Max</th>
                  </tr>
                </thead>
                <tbody>
                  {marksSummary?.internalMarks?.map(im => (
                    <tr key={im.id}>
                      <td style={{ fontWeight: 600 }}>{im.subject?.name} ({im.subject?.code})</td>
                      <td><span className="badge badge-info">{im.markType}</span></td>
                      <td style={{ fontWeight: 700 }}>{im.marksObtained}</td>
                      <td style={{ color: '#64748B' }}>{im.maxMarks}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>

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
        </div>
      )}

      {/* Certificate Upload & Verification Status */}
      {(activeTab === 'dashboard' || activeTab === 'my-certificates') && (
        <div className="grid-2">
          <div className="glass-card">
            <h3 style={{ fontSize: '1.1rem', fontWeight: 700, color: '#0F172A', marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <Upload color="#06B6D4" size={20} /> Upload New Certificate
            </h3>
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
            <h3 style={{ fontSize: '1.1rem', fontWeight: 700, color: '#0F172A', marginBottom: '1rem' }}>Uploaded Certificates</h3>
            <div className="table-container">
              <table className="custom-table">
                <thead>
                  <tr>
                    <th>Title</th>
                    <th>Status</th>
                    <th>Reviewer Feedback</th>
                  </tr>
                </thead>
                <tbody>
                  {certificates.map(cert => (
                    <tr key={cert.id}>
                      <td style={{ fontWeight: 600 }}>{cert.title}</td>
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
    </div>
  );
};
