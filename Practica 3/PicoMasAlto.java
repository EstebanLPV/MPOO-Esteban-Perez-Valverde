  public static int longestPeak(int[] array) {
        int maxLength = 0;
        for (int i = 1; i < array.length - 1; i++) {
            boolean isPeak = array[i] > array[i - 1] + 1
                          && array[i] > array[i + 1];
            if (isPeak) {
                int left = i - 1;
                int right = i + 1;

                while (left > 0 &&
                       array[left] > array[left - 1] + 1) {

                    left--;
                }
                while (right < array.length - 1 &&
                       array[right] > array[right + 1]) {

                    right++;
                }
                int length = right - left + 1;
                if (length > maxLength) {
                    maxLength = length;
                }
            }
        }
        return maxLength;
    }
}
