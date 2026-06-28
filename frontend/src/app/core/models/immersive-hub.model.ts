export interface ImmersiveSession {
  modelName: string;
  aiScore: number;
  notesGenerated: number;
  currentTranscriptIndex: number;
}

export interface Peer {
  name: string;
  avatarUrl: string;
  level: string;
}
