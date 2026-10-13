export function roleHome(role) {
  if (role === 'ADMIN') return '/admin';
  if (role === 'AGENT') return '/agent';
  return '/customer';
}
