import apiInstance from './axios';

export const authAPI = {
  firebaseLogin: async (firebaseToken) => {
    const response = await apiInstance.post('/auth/firebase-login', { token: firebaseToken });
    return response.data;
  },
  getMe: async () => {
    const response = await apiInstance.get('/auth/me');
    return response.data;
  },
  upgrade: async () => {
    const response = await apiInstance.post('/auth/upgrade');
    return response.data;
  },
  downgrade: async () => {
    const response = await apiInstance.post('/auth/downgrade');
    return response.data;
  }
};

export const instagramAPI = {
  connect: async (fbAccessToken) => {
    const response = await apiInstance.post('/instagram/connect', { accessToken: fbAccessToken });
    return response.data;
  },
  getAccount: async () => {
    const response = await apiInstance.get('/instagram/account');
    return response.data;
  },
  getMedia: async () => {
    const response = await apiInstance.get('/instagram/media');
    return response.data;
  }
};

export const automationAPI = {
  create: async (data) => {
    const response = await apiInstance.post('/automations', data);
    return response.data;
  },
  getAutomations: async () => {
    const response = await apiInstance.get('/automations');
    return response.data;
  },
  toggle: async (id) => {
    const response = await apiInstance.put(`/automations/${id}/toggle`);
    return response.data;
  },
  delete: async (id) => {
    const response = await apiInstance.delete(`/automations/${id}`);
    return response.data;
  },
  getStats: async () => {
    const response = await apiInstance.get('/automations/stats');
    return response.data;
  }
};

export const paymentsAPI = {
  createOrder: async (amount) => {
    const response = await apiInstance.post('/payments/create-order', {
      amount,
      currency: "INR",
      receipt: `rcpt_${new Date().getTime()}`
    });
    return response.data;
  },
  verifyPayment: async (data) => {
    const response = await apiInstance.post('/payments/verify-payment', data);
    return response.data;
  },
  createSubscription: async () => {
    const response = await apiInstance.post('/payments/create-subscription');
    return response.data;
  },
  verifySubscription: async (data) => {
    const response = await apiInstance.post('/payments/verify-subscription', data);
    return response.data;
  }
};

export const supportAPI = {
  createTicket: async (data) => {
    const response = await apiInstance.post('/support/tickets', data);
    return response.data;
  }
};
