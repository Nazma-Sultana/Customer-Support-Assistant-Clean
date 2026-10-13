const STATUS_LABELS = {
  OPEN: 'Open',
  IN_PROGRESS: 'In Progress',
  CLOSED: 'Closed',
};

export default function StatusBadge({ status }) {
  return (
    <span className={`badge status-${status?.toLowerCase()}`}>
      {STATUS_LABELS[status] || status}
    </span>
  );
}
