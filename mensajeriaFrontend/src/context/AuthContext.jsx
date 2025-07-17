// src/context/AuthContext.jsx
import { createContext, useContext, useEffect, useState } from 'react';
import { jwtDecode } from 'jwt-decode';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [auth, setAuth] = useState({ user: null, role: null });

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (token) {
      try {
        const decoded = jwtDecode(token);
        const roles = decoded.authorities || decoded.roles || [];
        const roleNames = roles.map(r => typeof r === 'string' ? r : r.authority || r);
        setAuth({ user: decoded.sub, role: roleNames[0] });
      } catch (err) {
        console.error("Error decoding token:", err);
      }
    }
  }, []);

  const login = (tokenObject) => {
    try {
      const {token, mensajeriaId, tenantId} = tokenObject;
      const additionalData = {
        mensajeriaId,
        tenantId
      }
      const additionalDataString = JSON.stringify(additionalData);
      if (!token || typeof token !== 'string') {
        throw new Error('Token inválido: debe ser un string');
      }
      
      localStorage.setItem('token', token);
      localStorage.setItem('x-additional-data', additionalDataString)
      const decoded = jwtDecode(token);
      const roles = decoded.authorities || decoded.roles || [];
      
      const roleNames = roles.map(r => typeof r === 'string' ? r : r.authority || r);
      
      setAuth({ user: decoded.sub, role: roleNames[0] });
    } catch (error) {
      throw error;
    }
  };

  const logout = () => {
    localStorage.removeItem('token');
    setAuth({ user: null, role: null });
  };

  return (
    <AuthContext.Provider value={{ auth, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
