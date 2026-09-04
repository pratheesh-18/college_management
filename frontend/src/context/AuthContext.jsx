import React, { createContext, useContext, useState, useEffect } from 'react';
import { apiService } from '../services/api';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem('edusphere_token');
    const storedUser = localStorage.getItem('edusphere_user');
    if (token && storedUser) {
      setUser(JSON.parse(storedUser));
      // Refresh user profile asynchronously
      apiService.getCurrentUser()
        .then(profile => {
          const updatedUser = { ...JSON.parse(storedUser), profile };
          setUser(updatedUser);
          localStorage.setItem('edusphere_user', JSON.stringify(updatedUser));
        })
        .catch(() => {
          // Token might be expired
          logout();
        })
        .finally(() => setLoading(false));
    } else {
      setLoading(false);
    }
  }, []);

  const login = async (email, password) => {
    const data = await apiService.login({ email, password });
    localStorage.setItem('edusphere_token', data.token);
    const userInfo = {
      id: data.id,
      email: data.email,
      fullName: data.fullName,
      roles: data.roles,
      profileId: data.profileId,
      departmentName: data.departmentName,
      className: data.className
    };
    setUser(userInfo);
    localStorage.setItem('edusphere_user', JSON.stringify(userInfo));
    return userInfo;
  };

  const logout = () => {
    localStorage.removeItem('edusphere_token');
    localStorage.removeItem('edusphere_user');
    setUser(null);
  };

  const primaryRole = user?.roles?.includes('ROLE_ADMIN') ? 'ADMIN'
    : user?.roles?.includes('ROLE_FACULTY') ? 'FACULTY'
    : user?.roles?.includes('ROLE_STUDENT') ? 'STUDENT'
    : 'GUEST';

  return (
    <AuthContext.Provider value={{ user, primaryRole, login, logout, loading }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
