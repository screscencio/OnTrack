package br.com.oncast.ontrack.shared.messageCode;

public enum UploadMessageCode implements BaseMessageCode<UploadMessages> {
	FILE_SIZE_LIMIT {
		@Override
		public String selectMessage(final UploadMessages messages, final String... args) {
			return messages.fileSizeLimit(args[0]);
		}
	},
	UPLOAD_COMPLETE {
		@Override
		public String selectMessage(final UploadMessages messages, final String... args) {
			return messages.serverUploadComplete();
		}
	};

}
